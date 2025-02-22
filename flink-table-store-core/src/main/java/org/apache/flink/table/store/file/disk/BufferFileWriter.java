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

package org.apache.flink.table.store.file.disk;

import org.apache.flink.table.store.file.memory.Buffer;
import org.apache.flink.table.store.memory.MemorySegment;

import java.io.IOException;

/** Write {@link MemorySegment} to file. */
public interface BufferFileWriter extends FileIOChannel {

    /**
     * Writes the given block. The request may be executed synchronously, or asynchronously,
     * depending on the implementation.
     *
     * @throws IOException Thrown, when the writer encounters an I/O error.
     */
    void writeBlock(Buffer buffer) throws IOException;
}
