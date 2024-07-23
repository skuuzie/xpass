# Xpass

secure offline password manager Android app. a refreshing project

---

Usage is very easy, just set up a one-time password for future unlocking. 

Data-at-rest is secured with authenticated encryption algorithm (AEAD with AES-GCM 256 bit) through native code written in Go.

---

Tech/Implementation summary:
- Architecture best practices
- Hilt for automated DI
- Efficient Coroutine usage
- Room for local database
- Customized app security [module](app/libs/droidguard/) written in Go (compiled to shared library)
- Code shrinking/obfuscation enabled

---

i might continue this project to explore about keystore, biometric, and more ui capabilities 😃