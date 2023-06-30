package com.example.repository.mariadb;

import com.example.entity.PlaylistTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MariaDbPlaylistTrackRepository extends JpaRepository<PlaylistTrack, Void>, JpaSpecificationExecutor<PlaylistTrack> {

}