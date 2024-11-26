package com.example.selfstudybe.dtos.Authentication;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserInfo {
    String sub;
    String name;
    String given_name;
    String family_name;
    String picture;
    String email;
    boolean email_verified;
}
