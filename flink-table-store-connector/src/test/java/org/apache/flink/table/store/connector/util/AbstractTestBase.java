/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.table.store.connector.util;

import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.test.junit5.MiniClusterExtension;
import org.apache.flink.util.FileUtils;
import org.apache.flink.util.TestLogger;

import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

/** Similar to Flink's AbstractTestBase but using Junit5. */
public class AbstractTestBase extends TestLogger {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTestBase.class);

    private static final int DEFAULT_PARALLELISM = 4;

    @RegisterExtension
    protected static final MiniClusterExtension MINI_CLUSTER_EXTENSION =
            new MiniClusterExtension(
                    new MiniClusterResourceConfiguration.Builder()
                            .setNumberTaskManagers(1)
                            .setNumberSlotsPerTaskManager(DEFAULT_PARALLELISM)
                            .build());

    @TempDir protected static Path temporaryFolder;

    // ----------------------------------------------------------------------------------------------------------------
    //  Temporary File Utilities
    // ----------------------------------------------------------------------------------------------------------------

    protected String getTempDirPath() {
        return getTempDirPath("");
    }

    protected String getTempDirPath(String dirName) {
        return createAndRegisterTempFile(dirName).toString();
    }

    protected String getTempFilePath(String fileName) {
        return createAndRegisterTempFile(fileName).toString();
    }

    protected String createTempFile(String fileName, String contents) throws IOException {
        File f = createAndRegisterTempFile(fileName);
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
        f.createNewFile();
        FileUtils.writeFileUtf8(f, contents);
        return f.toString();
    }

    /** Create a subfolder to avoid returning the same folder when passing same file name. */
    protected File createAndRegisterTempFile(String fileName) {
        return new File(
                temporaryFolder.toFile(), String.format("%s/%s", UUID.randomUUID(), fileName));
    }
}
