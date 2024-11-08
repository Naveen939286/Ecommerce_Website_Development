package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//PaymentDTO represent the payment
public class PaymentDTO
{
    //this is application payment id
    private Long paymentId;
    private String paymentMethod;
    //payment gate way payment id
    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
    private String pgName;
}
