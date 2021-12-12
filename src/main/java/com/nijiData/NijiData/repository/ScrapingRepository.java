package com.nijiData.NijiData.repository;

import com.nijiData.NijiData.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.nio.channels.Channel;
import java.util.List;

@Repository
public interface ScrapingRepository extends JpaRepository<Member, Integer> {

    @Query(value = "select channel_id from member", nativeQuery = true)
    List<String> findChid();

    @Query(value = "select id from member where channel_id = ?1", nativeQuery = true)
    int findIdByChid(String chId);
}
