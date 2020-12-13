package org.viper75.whatsappclone.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Contact {
    private String uid;
    private String username;
    private String status;
    private String image;
}
