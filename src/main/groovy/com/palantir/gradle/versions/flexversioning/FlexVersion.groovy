// Copyright 2015 Palantir Technologies
//
// Licensed under the Apache License, Version 2.0 (the "License")
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.palantir.gradle.versions.flexversioning

/*
 * POGO containing all the versioning information.  It can be used by the
 * standard version property of Gradle.  In addition, the code can grab
 * the individual portions of the constructed version without having to
 * parse the resulting string.
 */
class FlexVersion implements Serializable {
    private static final long serialVersionUID = -1230434938069850636L;

    final String domain;
    final String gitHash;

    final int commitCount;

    final boolean dirty;
    final boolean isTag;


    public FlexVersion (String domain, int commitCount, String gitHash, boolean isTag, boolean dirty) {
        this.domain = domain;
        this.commitCount = commitCount;
        this.gitHash = gitHash;
        this.isTag = isTag;
        this.dirty = dirty;

        if (this.gitHash.length() > 12) {
            this.gitHash = this.gitHash.substring(0, 12);
        }
    }


    public String toString() {
        String version = null;
        if (this.isTag) {
            version = "${this.domain}";
        } else {
            version = "${this.domain}-${this.commitCount}-g${this.gitHash}";
        }

        if (this.dirty) {
            version = "${version}-dirty";
        }

        return version;
    }
}
