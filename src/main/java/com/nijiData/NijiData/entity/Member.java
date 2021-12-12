package com.nijiData.NijiData.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="member")
public class Member {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(name="name")
    private String name;

    @Column(name="channel_id")
    private String channelId;

    @Column(name="subscriber")
    private String subscriber;

    @Column(name="video_count")
    private String videoCount;

    @Column(name="thumbnail")
    private String thumbnail;

    @Column(name="published_at")
    private Date publishedAt;
}
