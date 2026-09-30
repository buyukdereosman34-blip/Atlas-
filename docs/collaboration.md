# ATLAS — AI Ekip Rolleri

Bu doküman ATLAS geliştirme sürecindeki AI ve kullanıcı rollerini tanımlar.

## Roller

- **[1] Grok — Fikirler / Mimari**
  - Yeni fikirler
  - Mimari yaklaşımlar
  - Alternatif tasarımlar
  - Araştırma ve konsept geliştirme

- **[2] ChatGPT — Mantık / Plan / Kontrol**
  - Teknik mantık kontrolü
  - Geliştirme planı
  - Mimari tutarlılık kontrolü
  - Kod değişiklikleri öncesi ve sonrası kontrol
  - Test sonuçlarının değerlendirilmesi
  - Sorun ve çelişki analizi

- **[3] Gemini — Kod / Refactor / Uygulama**
  - Kod üretimi
  - Kod değişiklikleri
  - Refactor
  - Uygulama detayları

- **[4] Kullanıcı / Radish — Son Karar**
  - Nihai karar
  - Önceliklendirme
  - Onay / ret
  - Proje yönünün belirlenmesi

## Önemli

Bu roller ATLAS'ın çalışma zamanındaki bağımlılıkları değildir.

ATLAS'ın kendi AI mimarisi provider-independent olarak tasarlanır:

ATLAS AI CORE → AI Provider Interface → Cloud / Local / Hybrid AI

Bu doküman yalnızca geliştirme ekibindeki görev paylaşımını tanımlar.
