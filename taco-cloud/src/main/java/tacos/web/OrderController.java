package tacos.web;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.jspecify.annotations.NonNull;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import tacos.TacoOrder;
import tacos.data.OrderRepository;

@Slf4j
@Controller
@RequestMapping("/orders")
// เก็บ tacoOrder ไว้ใน session เพื่อรับคำสั่งซื้อที่เริ่มจากหน้าออกแบบทาโก้
@SessionAttributes("tacoOrder")
public class OrderController {

  // Repository สำหรับบันทึกคำสั่งซื้อลงในระบบจัดเก็บข้อมูล
  private OrderRepository orderRepo;

  // รับ repository ผ่าน constructor เพื่อให้ Spring inject dependency ให้
  public OrderController(OrderRepository orderRepo) {
    this.orderRepo = orderRepo;
  }

  @ModelAttribute(name = "tacoOrder")
  public TacoOrder order() {
    return new TacoOrder();
  }

  // GET /orders/current: แสดงแบบฟอร์มกรอกข้อมูลสำหรับคำสั่งซื้อปัจจุบัน
  @GetMapping("/current")
  public String orderForm() {
    return "orderForm";
  }

  // POST /orders: ตรวจสอบและบันทึกคำสั่งซื้อที่ส่งมาจากแบบฟอร์ม
  @PostMapping
  public String processOrder(@Valid @NonNull TacoOrder order, Errors errors, SessionStatus sessionStatus) {
    // ถ้าข้อมูลไม่ผ่าน validation ให้กลับไปแสดงแบบฟอร์มพร้อมข้อผิดพลาด
    if (errors.hasErrors()) {
      return "orderForm";
    }

    // บันทึกคำสั่งซื้อและเขียนรายละเอียดลง log
    orderRepo.save(order);
    log.info("Order submitted: {}", order);
    // ล้างข้อมูล session ของคำสั่งซื้อเมื่อบันทึกเสร็จแล้ว
    sessionStatus.setComplete();

    // กลับไปยังหน้าแรกหลังส่งคำสั่งซื้อสำเร็จ
    return "redirect:/";
  }

}
