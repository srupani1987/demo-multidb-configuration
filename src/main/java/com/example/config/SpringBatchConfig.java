package com.example.config;

import com.example.entity.Employee;
import com.example.entity.PlaylistTrack;
import com.example.repository.mysql.MySqlPlaylistTrackRepository;
import com.example.repository.mysql.MysqlEmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig extends DefaultBatchConfigurer {

    private static final String QUERY_FIND_EMPLOYEE = "SELECT * FROM Employee";
    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private MysqlEmployeeRepository mysqlEmployeeRepository;
    private MySqlPlaylistTrackRepository mySqlPlaylistTrackRepository;
    private DataSource mariadbDatasource;
    private EntityManagerFactory entityManagerFactory;



    /*@Bean
    ItemReader<Employee> reader() {
        JdbcCursorItemReader<Employee> databaseReader = new
                JdbcCursorItemReader<>();

        databaseReader.setDataSource(mariadbDatasource);
        databaseReader.setSql(QUERY_FIND_EMPLOYEE);
        databaseReader.setRowMapper(new BeanPropertyRowMapper<>
                (Employee.class));

        return databaseReader;
    }*/

    /*@Bean
    public JdbcCursorItemReader<Employee> reader() {
        JdbcCursorItemReader<Employee> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(mariadbDatasource);
        reader.setSql("select employeeId, lastName, firstName, title from Employee");
        reader.setRowMapper(new EmployeeResultRowMapper());
        reader.setMaxRows(2);
        reader.setFetchSize(2);
        reader.setQueryTimeout(10000);
        return reader;
    }*/

    @Bean
    public JpaPagingItemReader<Employee> getJpaPagingItemReader() {
        return new JpaPagingItemReaderBuilder<Employee>()
                .name("Employee")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select e from Employee e")
                .pageSize(5)
                .build();
    }

    @Bean
    public JpaPagingItemReader<PlaylistTrack> getPlayListTrackerReader() {
        /*String sql = "select p from PlaylistTrack p";
        JpaNativeQueryProvider<PlaylistTrack> queryProvider = new JpaNativeQueryProvider<PlaylistTrack>();
        JpaPagingItemReader<PlaylistTrack> reader = new JpaPagingItemReader<>();
        queryProvider.setSqlQuery(sql);
        reader.setQueryProvider(queryProvider);
        queryProvider.setEntityClass(PlaylistTrack.class);
        //reader.setParameterValues(Collections.singletonMap("limit", 10));
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setPageSize(10);
        reader.setSaveState(true);
        return reader;*/

        return new JpaPagingItemReaderBuilder<PlaylistTrack>()
                .name("PlaylistTrack")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select p from PlaylistTrack p")
                .pageSize(1000)
                .build();
    }

    @Bean
    public EmployeeProcessor processor() {
        return new EmployeeProcessor();
    }

    public RepositoryItemWriter<Employee> writer() {
        RepositoryItemWriter<Employee> writer = new RepositoryItemWriter<>();
        writer.setRepository(mysqlEmployeeRepository);
        writer.setMethodName("save");
        return writer;
    }

    public RepositoryItemWriter<PlaylistTrack> playListTrackerWriter() {
        RepositoryItemWriter<PlaylistTrack> writer = new RepositoryItemWriter<>();
        writer.setRepository(mySqlPlaylistTrackRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("mariadb-mysql").<Employee, Employee>chunk(2)
                .reader(getJpaPagingItemReader())
                .processor(processor())
                .writer(writer())
                .build();

    }

    @Bean
    public Step playListTracker() {
        return stepBuilderFactory.get("playlist-mariadb-mysql").<PlaylistTrack, PlaylistTrack>chunk(10)
                .reader(getPlayListTrackerReader())
                //.processor(processor())
                .writer(playListTrackerWriter())
                .build();

    }

    @Bean
    public Job runJob() {
        return jobBuilderFactory.get("migrateEmployees")
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    @Qualifier("playListTrackerJob")
    public Job playListTrackerJob() {
        return jobBuilderFactory.get("migrate-playlist-tracker")
                .flow(playListTracker())
                .end()
                .build();
    }
}
