package me.crycry.field.repository;

import me.crycry.field.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface FieldRepository extends JpaRepository<Field, Long>{

     int deleteByFid(Long fid);
     @Query("select count(1) from Field f")
     int getFieldCount();


}
