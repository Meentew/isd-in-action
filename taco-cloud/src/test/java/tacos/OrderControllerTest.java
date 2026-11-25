package tacos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tacos.data.OrderRepository;
import tacos.web.OrderController;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private OrderRepository orderRepository;

  @Test
  void orderFormCanBeOpenedWithoutExistingOrderInSession() throws Exception {
    mockMvc.perform(get("/orders/current"))
        .andExpect(status().isOk())
        .andExpect(view().name("orderForm"))
        .andExpect(model().attributeExists("tacoOrder"));
  }
}
