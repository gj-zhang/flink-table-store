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

package org.apache.flink.table.store.file.format;

import org.apache.flink.api.common.serialization.BulkWriter;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.store.data.InternalRow;
import org.apache.flink.table.store.file.predicate.Predicate;
import org.apache.flink.table.store.format.FileFormat;
import org.apache.flink.table.store.format.FormatReaderFactory;
import org.apache.flink.table.store.types.RowType;

import javax.annotation.Nullable;

import java.io.IOException;
import java.util.List;

/** A special {@link FileFormat} which flushes for every added element. */
public class FlushingFileFormat extends FileFormat {

    private final FileFormat format;

    public FlushingFileFormat(String identifier) {
        super(identifier);
        this.format = FileFormat.fromIdentifier(identifier, new Configuration());
    }

    @Override
    public FormatReaderFactory createReaderFactory(
            RowType type, int[][] projection, @Nullable List<Predicate> filters) {
        return format.createReaderFactory(type, projection, filters);
    }

    @Override
    public BulkWriter.Factory<InternalRow> createWriterFactory(RowType type) {
        return fsDataOutputStream -> {
            BulkWriter<InternalRow> wrapped =
                    format.createWriterFactory(type).create(fsDataOutputStream);
            return new BulkWriter<InternalRow>() {
                @Override
                public void addElement(InternalRow rowData) throws IOException {
                    wrapped.addElement(rowData);
                    wrapped.flush();
                }

                @Override
                public void flush() throws IOException {
                    wrapped.flush();
                }

                @Override
                public void finish() throws IOException {
                    wrapped.finish();
                }
            };
        };
    }
}
