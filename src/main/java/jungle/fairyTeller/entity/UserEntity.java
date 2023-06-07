package jungle.fairyTeller.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String userid;

    @Column(nullable = false)
    private String nickname;

    private String password;

    private String authorize;

    private String email;

    @CreationTimestamp
    private Date createdDate; // LocalDateTime

}
