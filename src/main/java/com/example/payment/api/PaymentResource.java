package com.example.payment.api;

import com.example.payment.domain.Payment;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.QueryParam;
import java.util.List;

@Path("/payments")
public class PaymentResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Payment> list(@QueryParam("merchantId") String merchantId,
                              @QueryParam("limit") Integer limit) {
        PanacheQuery<Payment> q;
        if (merchantId != null && !merchantId.isBlank()) {
            q = Payment.find("merchantId", merchantId);
        } else {
            q = Payment.findAll();
        }
        if (limit == null || limit <= 0) {
            limit = 50;
        }
        return q.page(0, Math.min(limit, 200)).list();
    }
}


