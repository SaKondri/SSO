package com.infrastructure.sso.dto.req.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * @author SaKondri
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attributes{
    private ArrayList<String> address;
    private ArrayList<String> phone;
    private ArrayList<String> zipCode;
}