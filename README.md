
- # FruitCrate – Pekiştirmeli Öğrenme Tabanlı Mobil Oyun



## Proje Hakkında
Bu projede, bir mobil oyun karakteri (kuş), çevresel etmenlere göre hareket etmeyi öğrenir. 
Karakter, meyve toplama, engellerden kaçınma ve olumsuz nesnelerden uzak durma görevlerini ödül–ceza mekanizması ile öğrenir.
Q-Learning algoritması kullanılarak karakterin zamanla daha başarılı kararlar alması hedeflenmiştir.

---

## İçindekiler
1. [Kullanılacak Algoritmalar ve Yazılım Mimarisi](#kullanılacak-algoritmalar-ve-yazılım-mimarisi)  
2. [Önerilen Yöntemin Geliştirilmesi](#önerilen-yöntemin-geliştirilmesi)  
3. [Özgünlük](#özgünlük)  
4. [Proje Takvimi](#proje-takvimi)  
5. [Referanslar](#referanslar)  

---

## Kullanılacak Algoritmalar ve Yazılım Mimarisi
- **Q-Learning (model-free, off-policy)**  
- Durum tanımı (state space) ve ödül şekillendirme (reward shaping)  
- Android platformu ve Kotlin dili  
- Modüler yapı: oyun mantığı, çizim işlemleri ve öğrenme algoritması ayrılmıştır

---

## Önerilen Yöntemin Geliştirilmesi
- **Durum Tanımı:** Meyve konumu, tuğla engel uzaklığı, zehirli nesne uzaklığı, karakter hızı  
- **Ödül Mekanizması:** Ara ödüller ile ajan davranışlarını optimize etme  
- **Performans Ölçütleri:** Toplanan meyve sayısı, çarpışma sayısı, toplam ödül, epsilon değişimi  

---

## Özgünlük
- **Heuristic Reward Shaping:** Ara davranışlar da ödüllendirilir  
- **Dinamik Tehlike Yönetimi:** Tuğla yakınlığında stres cezası  
- **Semantik State Encoding:** 24 duruma indirgenmiş kategoriler  
- **Zehir ve Yavaşlama Mekanizması:** Risk-getiri dengesini etkiler  
- **Çift Katsayılı Epsilon Decay:** Keşif-sömürü dengesini korur  

---

## Proje Takvimi
| Hafta | Aktivite |
|-------|----------|
| 1–6   | Literatür taraması ve Q-Learning incelemesi |
| 7–9   | Oyun senaryosu ve durum uzayının tasarlanması |
| 10–12 | Q-Learning algoritmasının kodlanması |
| 13–14 | Ödül mekanizması ve parametre ayarları |
| 15    | Testler, performans gözlemleri ve raporlama |

---

## Referanslar
1. R. S. Sutton ve A. G. Barto, *Reinforcement Learning: An Introduction*, 2. baskı, MIT Press, 2018  
2. Android Developers, "Canvas and Graphics," 2025. [Link](https://developer.android.com/develop/ui/views/graphics)  
3. wumo, "Reinforcement-Learning-An-Introduction: Kotlin implementation," GitHub, 2023. [Link](https://github.com/wumo/Reinforcement-Learning-An-Introduction)
