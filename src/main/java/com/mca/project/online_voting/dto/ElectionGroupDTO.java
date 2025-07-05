package com.mca.project.online_voting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ElectionGroupDTO {
    private Long id;
    private Long election_id;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MMM-dd")
    private Date start_date;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MMM-dd")
    private Date end_date;
    private String created_by;
    private String status;
}
