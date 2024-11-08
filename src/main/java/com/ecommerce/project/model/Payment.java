package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //storing payment id. It is auto generated
    private Long paymentID;

    //storing order against whom this payment is done
    @OneToOne(mappedBy = "payment", cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private Order order;

    @NotBlank
    @Size(min =4, message = "Payment method must contain atleasr 4 characters")
    //storing the mode of payment it would be online payment / cash on delivery
    private String paymentMethod;

    //PG means Payment Gate Way
    //here we are accepting pay gateway request response
    private String pgPaymentID;
    private String pgStatus;
    private String pgResponseMessage;
    // we have multiple payment gate ways  like paypal, Razorpay etc
    private String pgName;


    public Payment(String paymentMethod, String pgPaymentId, String pgStatus,
                   String pgResponseMessage, String pgName)
    {
        this.paymentMethod = paymentMethod;
        this.pgPaymentID = pgPaymentId;
        this.pgStatus = pgStatus;
        this.pgResponseMessage = pgResponseMessage;
        this.pgName = pgName;
    }
}
