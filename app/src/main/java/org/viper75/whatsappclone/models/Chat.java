package org.viper75.whatsappclone.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    private String name;
    private String message;
    private String time;
    private String unreadCount;
}
