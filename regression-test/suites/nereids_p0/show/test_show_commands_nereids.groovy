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

suite("test_show_commands_nereids") {
    checkNereidsExecute("show engines;")
    qt_cmd_1("show engines;")
    checkNereidsExecute("show storage engines;")
    qt_cmd_2("show storage engines;")    
    // can not use qt to check, the output may change.

    checkNereidsExecute("""show frontends;""")
    checkNereidsExecute("""show backends;""")
    checkNereidsExecute("""show whitelist;""")
    checkNereidsExecute("""show triggers;""")
    checkNereidsExecute("""show events;""")
    test {
        sql """show load profile "/";"""
        exception """show query/load profile syntax is a deprecated feature"""
    }
    test {
        sql """show query profile "/";"""
        exception """show query/load profile syntax is a deprecated feature"""
    }
}
