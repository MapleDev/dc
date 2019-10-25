package me.crycry.file.repository;

import me.crycry.file.entity.FieldResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface FieldResultRepository extends JpaRepository<FieldResult,Long>{

     List<FieldResult> findAllByTaskId(Long taskId,String fieldName);
     @Query("SELECT count(1) as count,f as result FROM FieldResult f  where f.taskId=?1 and f.fieldName=?2 GROUP BY f.taskId,f.file")
     List<Map<String,Object>> listRsultDistinct(Long taskId,String fieldName);

    @Query("SELECT DISTINCT f.fieldName FROM FieldResult f WHERE f.taskId=?1")
    List<String> listFieldNames(Long taskId);


    @Query("SELECT DISTINCT f.file FROM FieldResult f WHERE f.taskId=?1")
    List<String> listFiles(Long taskId);

    @Query("SELECT count(1) as count,f as result FROM FieldResult f  where f.taskId=?1 and f.file=?2 GROUP BY f.fieldName")
    List<Map<String,Object>> listRsultGroupByFieldName(Long taskId,String file);

    @Query("SELECT DISTINCT f.fieldName FROM FieldResult f WHERE task_id=?1")
    List<String> listResultFieldNames(Long taskId);

    int deleteByFile(String file);


    @Query("select  f from FieldResult f where f.taskId=?1 and f.frid=?2 ")
    FieldResult findByTaskIdAnAndFrid(Long taskId,Long frid);

    @Query("SELECT f FROM FieldResult f WHERE f.taskId=?1 AND f.fieldName=?2 AND f.file=?3")
    List<FieldResult> listMultiResult(Long taskId,String fieldName,String file);

    @Transactional
    @Modifying
    @Query("delete from FieldResult f where f.taskId=?1 and f.frid<>?2 and f.fieldName=?3 and f.file=?4")
    int checkResult(Long taskId,Long frid,String fieldName,String file);



}
