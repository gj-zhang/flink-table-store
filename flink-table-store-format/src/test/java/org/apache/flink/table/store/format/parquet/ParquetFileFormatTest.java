/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.table.store.format.parquet;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DelegatingConfiguration;

import org.apache.parquet.format.CompressionCodec;
import org.apache.parquet.hadoop.ParquetOutputFormat;
import org.junit.jupiter.api.Test;

import static org.apache.flink.table.store.format.parquet.ParquetFileFormat.getParquetConfiguration;
import static org.apache.flink.table.store.format.parquet.ParquetFileFormatFactory.IDENTIFIER;
import static org.assertj.core.api.Assertions.assertThat;

/** Test for {@link ParquetFileFormatFactory}. */
public class ParquetFileFormatTest {
    private static final ConfigOption<String> KEY1 =
            ConfigOptions.key("k1").stringType().defaultValue("absent");

    @Test
    public void testAbsent() {
        Configuration options = new Configuration();
        ParquetFileFormat parquet = new ParquetFileFormatFactory().create(options);
        assertThat(parquet.formatOptions().getString(KEY1)).isEqualTo("absent");
    }

    @Test
    public void testPresent() {
        Configuration options = new Configuration();
        options.setString(KEY1.key(), "v1");
        ParquetFileFormat parquet = new ParquetFileFormatFactory().create(options);
        assertThat(parquet.formatOptions().getString(KEY1)).isEqualTo("v1");
    }

    @Test
    public void testDefaultCompressionCodecName() {
        Configuration conf = new Configuration();
        assertThat(getCompressionCodec(conf)).isEqualTo(CompressionCodec.SNAPPY.name());
    }

    @Test
    public void testSpecifiedCompressionCodecName() {
        String lz4 = CompressionCodec.LZ4.name();
        Configuration conf = new Configuration();
        conf.setString(ParquetOutputFormat.COMPRESSION, lz4);
        assertThat(getCompressionCodec(conf)).isEqualTo(lz4);
    }

    private String getCompressionCodec(Configuration conf) {
        DelegatingConfiguration formatOptions = new DelegatingConfiguration(conf, IDENTIFIER + ".");
        ParquetFileFormat parquet = new ParquetFileFormatFactory().create(formatOptions);
        return getParquetConfiguration(parquet.formatOptions())
                .getString(ParquetOutputFormat.COMPRESSION, null);
    }
}
