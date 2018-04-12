/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.company.ice.mygmail.utils;

/**
 * Created by amitshekhar on 08/01/17.
 */

public final class AppConstants {

    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    public static final int REQUEST_PERMISSION_EXTERNAL_STORAGE = 1004;

    public final static class MIME_TYPE {
        public static final String TEXT_PLAIN = "text/plain";
        public static final String TEXT_HTML = "text/html";
        public static final String MULTIPAR_RELATED = "multipart/related";
        public static final String MULTIPAR_MIXED = "multipart/mixed";
        public static final String MULTIPAR_ALTERNATIVE = "multipart/alternative";
    }

    public static final int READ_REQUEST_CODE = 42;

    public static final String SHARED_PREFERENCE_TAG = "MyGmail";
    public static final String PREF_ACCOUNT_NAME = "accountName";

    public final static class MESSAGE_LABELS {
        public static final String INBOX = "INBOX";
        public static final String STARRED = "STARRED";
        public static final  String IMPORTANT = "IMPORTANT";
        public static final  String SENT = "SENT";
        public static final  String DRAFTS = "DRAFTS";
        public static final  String SPAM = "SPAM";
        public static final  String TRASH = "TRASH";
        public static final  String UNREAD = "UNREAD";
    }


//    public static final String DB_NAME = "mindorks_mvp.db";
//    public static final String PREF_NAME = "mindorks_pref";
//
//    public static final long NULL_INDEX = -1L;
//
//    public static final String SEED_DATABASE_OPTIONS = "seed/options.json";
//    public static final String SEED_DATABASE_QUESTIONS = "seed/questions.json";

    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";

    private AppConstants() {
        // This utility class is not publicly instantiable
    }
}
