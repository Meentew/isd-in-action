package tacos.web;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import lombok.extern.slf4j.Slf4j;
import tacos.Ingredient;
import tacos.Ingredient.Type;
import tacos.TacoOrder;
import tacos.Taco;
import tacos.data.IngredientRepository;

@Slf4j
@Controller
@RequestMapping("/design") //เป็นการกำหนด URL ของ Controller นี้ให้เป็น /design
@SessionAttributes("tacoOrder")//เป็นการกำหนดให้ Spring MVC เก็บข้อมูลของ tacoOrder ไว้ใน session ของผู้ใช้
public class DesignTacoController {

  // Repository สำหรับอ่านข้อมูลวัตถุดิบจากฐานข้อมูล
  private final IngredientRepository ingredientRepo;

  // รับ repository ผ่าน constructor เพื่อให้ Spring inject dependency ให้
  public DesignTacoController(
        IngredientRepository ingredientRepo) {
    this.ingredientRepo = ingredientRepo;
  }

  // เตรียมรายการวัตถุดิบแยกตามประเภทไว้ใน Model เพื่อให้หน้า design นำไปแสดงผล
  @ModelAttribute //เป็นการกำหนดให้ Spring MVC เรียกใช้เมธอดนี้ก่อนที่จะเรียกใช้เมธอดอื่น ๆ ใน Controller นี้
  public void addIngredientsToModel(Model model) {
    List<Ingredient> ingredients = new ArrayList<>();
    // โหลดวัตถุดิบทั้งหมดจาก repository
    ingredientRepo.findAll().forEach(i -> ingredients.add(i));

    // สร้าง Model attribute หนึ่งรายการต่อ Ingredient.Type เช่น wrap, protein หรือ sauce
    Type[] types = Ingredient.Type.values();
    for (Type type : types) {
      model.addAttribute(type.name().toLowerCase(), 
          filterByType(ingredients, type));//เป็นการเพิ่มข้อมูลของ ingredients ที่ถูกกรองตามประเภทของ ingredient ลงใน model โดยใช้ชื่อของประเภทเป็น key
    }
  }

  // สร้างหรือดึง TacoOrder จาก Model; ใช้ session ตาม @SessionAttributes ด้านบน
  @ModelAttribute(name = "tacoOrder") //เป็นการสร้าง object ของ TacoOrder และเพิ่มลงใน model โดยใช้ชื่อว่า "tacoOrder"
  public TacoOrder order() {
    return new TacoOrder();
  }

  // สร้าง Taco ว่างเพื่อใช้ผูกค่าจากฟอร์มออกแบบ
  @ModelAttribute(name = "taco")//เป็นการสร้าง object ของ Taco และเพิ่มลงใน model โดยใช้ชื่อว่า "taco"
  public Taco taco() {
    return new Taco(); 
  }

  // GET /design: เปิดหน้าแบบฟอร์มออกแบบทาโก้
  @GetMapping //เป็นการกำหนดให้เมธอดนี้ถูกเรียกเมื่อมีการส่งคำขอ GET มาที่ URL /design
  public String showDesignForm() {
    return "design";
  }

  // POST /design: รับข้อมูลจากฟอร์มและตรวจสอบความถูกต้องก่อนเพิ่ม Taco ลงในคำสั่งซื้อ
  @PostMapping
  public String processTaco(
      @Valid Taco taco, Errors errors,
      @ModelAttribute TacoOrder tacoOrder) {

    // หาก validation ไม่ผ่าน ให้แสดงฟอร์มเดิมพร้อมข้อความผิดพลาด
    if (errors.hasErrors()) {
      return "design";
    }

    // เพิ่ม Taco ที่ผ่าน validation ลงในคำสั่งซื้อปัจจุบัน แล้วบันทึกข้อมูลลง log
    tacoOrder.addTaco(taco);
    log.info("Taco submitted: {}", taco);

    // ส่งผู้ใช้ไปหน้าสรุป/ดำเนินการกับคำสั่งซื้อปัจจุบัน
    return "redirect:/orders/current";
  }

  // เลือกเฉพาะวัตถุดิบที่มีประเภทตรงกับประเภทที่ระบุ
  private Iterable<Ingredient> filterByType(
      List<Ingredient> ingredients, Type type) {
    return ingredients
              .stream()
              .filter(x -> x.getType().equals(type))
              .collect(Collectors.toList());
  }

}
