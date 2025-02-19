// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.doris.datasource;

import org.apache.doris.analysis.CreateDbStmt;
import org.apache.doris.analysis.CreateTableStmt;
import org.apache.doris.analysis.DropDbStmt;
import org.apache.doris.analysis.DropTableStmt;
import org.apache.doris.analysis.TableName;
import org.apache.doris.analysis.TableValuedFunctionRef;
import org.apache.doris.analysis.TruncateTableStmt;
import org.apache.doris.catalog.DatabaseIf;
import org.apache.doris.catalog.Env;
import org.apache.doris.catalog.TableIf;
import org.apache.doris.common.AnalysisException;
import org.apache.doris.common.DdlException;
import org.apache.doris.common.ErrorCode;
import org.apache.doris.common.MetaNotFoundException;
import org.apache.doris.common.Pair;
import org.apache.doris.common.UserException;
import org.apache.doris.nereids.trees.expressions.functions.table.TableValuedFunction;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;

/**
 * The interface of Catalog
 */
public interface CatalogIf<T extends DatabaseIf> {
    Logger LOG = LogManager.getLogger(CatalogIf.class);

    // Type of this catalog
    String getType();

    long getId();

    // Name of this catalog
    String getName();

    List<String> getDbNames();

    default boolean isInternalCatalog() {
        return this instanceof InternalCatalog;
    }

    // Will be used when querying the information_schema table
    // Unable to get db for uninitialized catalog to avoid query timeout
    default List<String> getDbNamesOrEmpty() {
        try {
            return getDbNames();
        } catch (Exception e) {
            LOG.warn("failed to get db names in catalog {}", getName(), e);
            return Lists.newArrayList();
        }
    }

    List<Long> getDbIds();

    @Nullable
    T getDbNullable(String dbName);

    @Nullable
    T getDbNullable(long dbId);

    Map<String, String> getProperties();

    default String getResource() {
        return null;
    }

    default void notifyPropertiesUpdated(Map<String, String> updatedProps) {
        if (this instanceof ExternalCatalog) {
            ((ExternalCatalog) this).onRefresh(false);
        }
    }

    void modifyCatalogName(String name);

    void modifyCatalogProps(Map<String, String> props);

    default Optional<T> getDb(String dbName) {
        return Optional.ofNullable(getDbNullable(dbName));
    }

    default Optional<T> getDb(long dbId) {
        return Optional.ofNullable(getDbNullable(dbId));
    }

    default <E extends Exception> T getDbOrException(String dbName, Function<String, E> e) throws E {
        T db = getDbNullable(dbName);
        if (db == null) {
            throw e.apply(dbName);
        }
        return db;
    }

    default <E extends Exception> T getDbOrException(long dbId, Function<Long, E> e) throws E {
        T db = getDbNullable(dbId);
        if (db == null) {
            throw e.apply(dbId);
        }
        return db;
    }

    default T getDbOrMetaException(String dbName) throws MetaNotFoundException {
        return getDbOrException(dbName,
                s -> new MetaNotFoundException("unknown databases, dbName=" + s, ErrorCode.ERR_BAD_DB_ERROR));
    }

    default T getDbOrMetaException(long dbId) throws MetaNotFoundException {
        return getDbOrException(dbId,
                s -> new MetaNotFoundException("unknown databases, dbId=" + s, ErrorCode.ERR_BAD_DB_ERROR));
    }

    default T getDbOrDdlException(String dbName) throws DdlException {
        return getDbOrException(dbName,
                s -> new DdlException(ErrorCode.ERR_BAD_DB_ERROR.formatErrorMsg(s), ErrorCode.ERR_BAD_DB_ERROR));
    }

    default T getDbOrDdlException(long dbId) throws DdlException {
        return getDbOrException(dbId,
                s -> new DdlException(ErrorCode.ERR_BAD_DB_ERROR.formatErrorMsg(s), ErrorCode.ERR_BAD_DB_ERROR));
    }

    default T getDbOrAnalysisException(String dbName) throws AnalysisException {
        return getDbOrException(dbName,
                s -> new AnalysisException(ErrorCode.ERR_BAD_DB_ERROR.formatErrorMsg(s), ErrorCode.ERR_BAD_DB_ERROR));
    }

    default T getDbOrAnalysisException(long dbId) throws AnalysisException {
        return getDbOrException(dbId,
                s -> new AnalysisException(ErrorCode.ERR_BAD_DB_ERROR.formatErrorMsg(s), ErrorCode.ERR_BAD_DB_ERROR));
    }

    // Called when catalog is dropped
    default void onClose() {
        Env.getCurrentEnv().getRefreshManager().removeFromRefreshMap(getId());
    }

    String getComment();

    default void setComment(String comment) {
    }

    default long getLastUpdateTime() {
        return -1L;
    }

    default CatalogLog constructEditLog() {
        CatalogLog log = new CatalogLog();
        log.setCatalogId(getId());
        log.setCatalogName(getName());
        log.setResource(Strings.nullToEmpty(getResource()));
        log.setComment(getComment());
        log.setProps(getProperties());
        return log;
    }

    TableName getTableNameByTableId(Long tableId);

    // Return a copy of all db collection.
    Collection<DatabaseIf<? extends TableIf>> getAllDbs();

    boolean enableAutoAnalyze();

    void createDb(CreateDbStmt stmt) throws DdlException;

    default void dropDb(DropDbStmt stmt) throws DdlException {
        dropDb(stmt.getDbName(), stmt.isSetIfExists(), stmt.isForceDrop());
    }

    void dropDb(String dbName, boolean ifExists, boolean force) throws DdlException;

    /**
     * @return if org.apache.doris.analysis.CreateTableStmt.ifNotExists is true, return true if table exists,
     * return false otherwise
     */
    boolean createTable(CreateTableStmt stmt) throws UserException;

    void dropTable(DropTableStmt stmt) throws DdlException;

    void dropTable(String dbName, String tableName, boolean isView, boolean isMtmv, boolean ifExists,
                   boolean force) throws DdlException;

    void truncateTable(TruncateTableStmt truncateTableStmt) throws DdlException;

    /**
     * Try to parse meta table name from table name.
     * Some catalog allow querying meta table like "table_name$partitions".
     * Catalog can override this method to parse meta table name from table name.
     *
     * @param tableName table name like "table_name" or "table_name$partitions"
     * @return pair of source table name and meta table name
     */
    default Pair<String, String> getSourceTableNameWithMetaTableName(String tableName) {
        return Pair.of(tableName, "");
    }

    default Optional<TableValuedFunction> getMetaTableFunction(String dbName, String sourceNameWithMetaName) {
        return Optional.empty();
    }

    default Optional<TableValuedFunctionRef> getMetaTableFunctionRef(String dbName, String sourceNameWithMetaName) {
        return Optional.empty();
    }

    // Convert from remote database name to local database name, overridden by subclass if necessary
    default String fromRemoteDatabaseName(String remoteDatabaseName) {
        return remoteDatabaseName;
    }

    // Convert from remote table name to local table name, overridden by subclass if necessary
    default String fromRemoteTableName(String remoteDatabaseName, String remoteTableName) {
        return remoteTableName;
    }
}
