# ATLAS — ChatGPT ile Geliştirme Geçmişi

**Son güncelleme:** 2026-10-20
**Amaç:** ChatGPT ile yapılan geliştirme çalışmalarını, mimari kararları,
hata ayıklama yöntemlerini ve gelecekteki özellikleri kalıcı olarak saklamak.

---

## 1. ChatGPT'nin ATLAS Projesindeki Rolü

ChatGPT'nin sorumlulukları:

- Mimari ve teknik mantığı kontrol etmek.
- Geliştirme adımlarını planlamak.
- Kod değişikliklerinden önce mevcut yapıyı incelemek.
- Hata mesajlarını ve test sonuçlarını değerlendirmek.
- Çelişkileri ve olası riskleri belirlemek.
- Minimum değişiklikle güvenli çözüm önermek.
- Mevcut proje notlarını ve önceki kararları korumak.

ChatGPT'nin önerileri otomatik olarak nihai karar sayılmaz.
Son karar kullanıcıya aittir.

## 2. ATLAS'ın Temel Mimari İlkesi

ATLAS, Android ve Termux üzerinde geliştirilen,
AI sağlayıcısından bağımsız bir geliştirme ve çalışma platformudur.

Temel akış:

User
→ AI Agent
→ Intent / Task
→ Planner
→ Tool Selection
→ Policy / Permission
→ Runtime
→ Backend
→ Android / Termux
→ Observation
→ AI Evaluation

Temel kural:

- AI karar verir.
- Tool, yapabileceği işlemi tanımlar.
- Policy ve Permission, işlemin sınırlarını belirler.
- Runtime, işlemi yürütür.
- Backend, yürütme mekanizmasını sağlar.
- Android veya Termux, ilgili işlemi gerçekleştirir.

Bileşenlerin görevleri birbirine karıştırılmamalıdır.

## 3. Birlikte Belirlenen Geliştirme Kuralları

1. Önce kanıt, sonra değişiklik.
2. Kod değişikliğinden önce mevcut dosyaları incele.
3. Gereksiz yeniden yazım yapma.
4. Çalışan Java kodunu yalnızca modern görünmesi için Kotlin'e çevirme.
5. Küçük ve hedefli değişiklikler yap.
6. Değişiklikten sonra uygun testleri çalıştır.
7. Test başarısızlıklarını gizleme; @Ignore kullanma.
8. Ortam sorunlarıyla kod hatalarını birbirinden ayır.
9. Build SUCCESS, özelliğin doğrulandığı anlamına gelmez.
10. Onay gerektiren işlemleri kullanıcı onayı olmadan gerçekleştirme.
11. Başarısız deneylerin sonuçlarını ve nedenlerini kaydet.
12. Mevcut çalışan özellikleri koru.

## 4. Self-Development ve LAB

ATLAS'ın kendini geliştirme akışında hedeflenen yapı:

Proposal
→ Safety Check
→ LAB Workspace
→ Apply Change
→ Build / Test
→ Evaluation
→ Retry veya Stop
→ Approval
→ Promotion
→ Learning Record

Kurallar:

- Deneyler mümkün olduğunca izole LAB ortamında yürütülür.
- Değişikliklerin hangi dosyaları etkilediği açıkça belirtilir.
- Başarısız sonuçlar gizlenmez.
- Ortam kaynaklı hatalarda gereksiz tekrar yapılmaz.
- Doğrulanmamış değişiklikler başarılı olarak raporlanmaz.
- Geri dönüş ve checkpoint mekanizması geliştirilmelidir.
- Ana projeye aktarım güvenlik ve doğrulama koşullarına bağlı olmalıdır.

## 5. Build ve Test Hataları

AAPT2 sorunu geçmiş geliştirme notlarında kayıtlıdır.

Java-only fallback kullanılırsa:

- Java derlemesi başarılı olabilir.
- Android kaynakları doğrulanmamış kalabilir.
- Testler çalıştırılmamış olabilir.

Bu nedenle yalnızca Java derlemesi başarılı olduğunda
tüm Android projesinin doğrulandığı söylenmemelidir.

Doğru raporlama, gerçekten çalıştırılan kontrolleri belirtmelidir.

## 6. Project Memory

ATLAS'ın kalıcı hafızası zaman içinde şu bilgileri korumalıdır:

- Mimari kararlar
- Geliştirme geçmişi
- Tamamlanan ve eksik özellikler
- Test sonuçları
- Bilinen hatalar
- Başarısız deneyler ve nedenleri
- Askıya alınan fikirler
- Sonraki adımlar

Yeni kayıtlar eski kararları sessizce silmemelidir.
Çelişen bilgiler tarih ve kaynaklarıyla birlikte saklanmalıdır.

Bu belge, Project Memory'nin yerine geçmez;
geliştirme sürecine ait ayrı bir kayıt olarak tutulur.

## 7. Özellik Envanteri

Uzun vadeli ATLAS kapsamı:

- AI Core ve sağlayıcı yönlendirme
- Agent ve Planner
- Tool System ve Runtime
- Android kontrol ve gözlemleme
- Terminal ve Project Engine
- Build / Test Engine
- Self-Development ve LAB
- Failure Classification ve Stuck Detection
- Project Memory
- Policy / Permission
- Checkpoint / Rollback
- Web ve açık kaynak araştırması
- Güvenlik analizi
- Oyun geliştirme ve mod desteği
- Oyun performansı ve kaynak yönetimi
- Çoklu AI ve çoklu ajan iş birliği
- ChatGPT Bridge ve Command Starter
- Obsidian ATLAS Knowledge-Project Mirror
- PRoot / RootFS alternatif yürütme ortamı
- ATLAS City, AI vatandaşları ve ekonomi
- Fiziksel donanım ve altyapı bütçesi

Bu liste, bütün özelliklerin tamamlandığı anlamına gelmez.
Her özellik mevcut kod incelenerek sınıflandırılmalıdır:
MEVCUT / KISMİ / EKSİK / ASKIDA.

## 8. Askıya Alınan veya Sonraya Bırakılan İşler

Aşağıdaki başlıklar unutulmamalıdır:

- Obsidian Knowledge-Project Mirror
- GitHub Copilot / GitHub AI Agent entegrasyonu araştırması
- PRoot / RootFS alternatif backend'i
- Yerel AI modellerinin genişletilmesi
- Çoklu ajan sistemi
- ATLAS City ekonomisi ve AI vatandaşları

Bunlar, mevcut geliştirme önceliğinin önüne otomatik olarak geçirilmemelidir.

## 9. ATLAS City

Uzun vadeli hedeflerden biri, AI vatandaşlarının görevler ve uzmanlıklar
üzerinden çalıştığı bir şehir/ekonomi katmanıdır.

Öngörülen bileşenler:

- AI vatandaşları, görevleri ve uzmanlıkları
- Görev performansı ve deneyim
- Dahili ATLAS AI kredileri
- Gerçek gelir ve gider kayıtları
- AI işletme giderleri
- Fiziksel donanım geliştirme bütçesi
- Kullanıcı tarafından yönetilen şehir politikası ve bütçesi

AI kredileri ile gerçek para birbirine karıştırılmamalıdır.
Gelir paylaşım oranları kesinleştirilmeden sayı uydurulmamalıdır.

## 10. Geliştirme İş Birliği

Önceden belirlenen çalışma rolleri:

- Grok: fikirler ve alternatif mimariler
- ChatGPT: mantık, planlama, mimari kontrol ve doğrulama
- Gemini: kodlama, refactor ve uygulama
- Kullanıcı / Radish: nihai karar ve önceliklendirme

Bu roller, ATLAS'ın kendi çalışma zamanındaki AI sağlayıcılarına
zorunlu bağımlılık oluşturmaz.

## 11. Her Yeni Oturum İçin Kontrol Listesi

1. Güncel proje notlarını oku.
2. Son bilinen durumu gerçek kodla karşılaştır.
3. Önceki oturumun varsayımlarını doğrulanmış gerçek kabul etme.
4. İlgili dosyaları incele.
5. Tek ve küçük bir sonraki adım belirle.
6. Değişiklikten sonra uygun testleri çalıştır.
7. Sonucu ve açık kalan sorunları notlara kaydet.

---

## Son Kural

**Kanıt → Minimum Değişiklik → Test → Sonuç Kaydı**

Bu belge geliştirme geçmişidir; uygulamanın çalışan kodunu değiştirmez.
