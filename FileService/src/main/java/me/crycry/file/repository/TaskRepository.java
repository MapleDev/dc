package me.crycry.file.repository;

import me.crycry.file.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task,Long>{

    @Query("select count(1) from Task t where t.status='已完成'")
    int queryFinishedTaskCount();

}
