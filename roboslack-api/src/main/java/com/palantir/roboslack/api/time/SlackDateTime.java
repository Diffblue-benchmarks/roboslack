/*
 * Copyright 2017 Palantir Technologies, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palantir.roboslack.api.time;

import com.palantir.roboslack.api.markdown.StringDecorator;
import com.palantir.roboslack.api.markdown.ValueDecorator;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Optional;

public final class SlackDateTime {

    private static final ValueDecorator<String> OUTPUT_DECORATOR = StringDecorator.of("<!date", ">");
    private static final String COMPONENT_FORMAT = "^%s";

    private long epochTimestamp;
    private SlackDateTimeFormat format;
    private Optional<URL> link;

    private SlackDateTime(long epochTimestamp, SlackDateTimeFormat format, Optional<URL> link) {
        this.epochTimestamp = epochTimestamp;
        this.format = format;
        this.link = link;
    }

    private static String computeFallbackText(SlackDateTimeFormat format, long epochTimestamp) {
        Instant instant = Instant.ofEpochSecond(epochTimestamp);
        return format.formatter().format(instant);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(String.format(COMPONENT_FORMAT, epochTimestamp));
        output.append(String.format(COMPONENT_FORMAT, format));
        link.ifPresent(l -> output.append(String.format(COMPONENT_FORMAT, l.toString())));
        output.append(String.format("|%s", computeFallbackText(format, epochTimestamp)));
        return OUTPUT_DECORATOR.decorate(output.toString());
    }

    public static final class Builder {
        private long epochTimestamp;
        private SlackDateTimeFormat format;
        private Optional<URL> link = Optional.empty();

        public Builder epochTimestamp(long value) {
            this.epochTimestamp = value;
            return this;
        }

        public Builder epochTimestamp(Temporal temporal) {
            this.epochTimestamp = EpochTimestampConverter.convert(temporal);
            return this;
        }

        public Builder format(SlackDateTimeFormat value) {
            this.format = value;
            return this;
        }

        public Builder link(URL value) {
            this.link = Optional.of(value);
            return this;
        }

        public SlackDateTime build() {
            return new SlackDateTime(this.epochTimestamp, this.format, this.link);
        }

    }

}
