package com.socialmediasaver.status.model.InstagramSearch;

public class InstaSearchUserDetailsModel {
    private Graphql graphql;

    public Graphql getGraphql() {
        return graphql;
    }

   public class Graphql{
        private User user;

        public User getUser() {
            return user;
        }
    }

    public class User {
        private EdgeFollowedBy edge_followed_by;
        private EdgeFollow edge_follow;
        String username;

        public EdgeFollowedBy getEdge_followed_by() {
            return edge_followed_by;
        }

        public EdgeFollow getEdge_follow() {
            return edge_follow;
        }

        public String getUsername() {
            return username;
        }

        public String getProfile_pic_url() {
            return profile_pic_url;
        }

        public String getProfile_pic_url_hd() {
            return profile_pic_url_hd;
        }

        String profile_pic_url;
        String profile_pic_url_hd;
    }
    public class EdgeFollowedBy {
        public Integer getCount() {
            return count;
        }

        private Integer count;
    }

    public class EdgeFollow {
        private Integer count;

        public Integer getCount() {
            return count;
        }
    }

}
