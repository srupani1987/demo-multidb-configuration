package com.example.repository.mysql;

import com.example.entity.PlaylistTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MySqlPlaylistTrackRepository extends JpaRepository<PlaylistTrack, Void>, JpaSpecificationExecutor<PlaylistTrack> {

}