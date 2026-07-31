# Project Rules

- Never run `assembleDebug` / APK builds to verify code. Use fast compile checks
  (`:shared:compileAndroidMain`) and tests instead. Only build an APK when the
  user explicitly asks for one.
