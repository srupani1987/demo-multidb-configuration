package com.example.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "PlaylistTrack")
public class PlaylistTrack implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "PlaylistId", nullable = false)
    private Integer playlistId;

    @Id
    @Column(name = "TrackId", nullable = false)
    private Integer trackId;

}
