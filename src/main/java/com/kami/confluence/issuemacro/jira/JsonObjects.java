package com.kami.confluence.issuemacro.jira;

import java.util.Collections;
import java.util.List;

public class JsonObjects {
    public static class CreateIssueRequest {
        private Fields fields;

        public CreateIssueRequest(final long projectId, final long issueTypeId, final String summary) {
            fields = new Fields();
            fields.project = new Fields.Project();
            fields.project.id = String.valueOf(projectId);
            fields.issuetype = new Fields.IssueType();
            fields.issuetype.id = String.valueOf(issueTypeId);
            fields.summary = summary;
        }

        private static class Fields {
            public Project project;
            public IssueType issuetype;
            public String summary;

            private static class Project {
                public String id;
            }

            private static class IssueType {
                public String id;
            }
        }
    }

    public static class CreateIssueResponse {
        public String key;
    }

    public static class UpdateIssueRequest {
        private Update update;

        public UpdateIssueRequest(final String summary) {
            update = new Update();
            Update.Set set = new Update.Set();
            set.set = summary;
            update.summary = Collections.singletonList(set);
        }

        private static class Update {
            public List<Set> summary;

            private static class Set {
                public String set;
            }
        }
    }

    public static class GetIssueResponse {
        public String key;
        public Fields fields;

        public static class Fields {
            public String summary;
        }
    }
}
