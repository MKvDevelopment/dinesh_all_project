package com.socialmediasaver.status.model.InstagramSearch;

import java.util.List;

public class InstagramSearchModel {

    public List<User> getUsers() {
        return users;
    }

    private List<User> users = null;


    public class User {
        private User__1 user;

        public User__1 getUser() {
            return user;
        }
    }


    public class User__1 {

        private String username;
        private String profile_pic_url;
        private String full_name;

        public String getProzile_pic_url() {
            return profile_pic_url;
        }

        private Boolean is_private;

        public String getUsername() {
            return username;
        }

        public String getFull_name() {
            return full_name;
        }

        public Boolean getIs_private() {
            return is_private;
        }
    }
}
