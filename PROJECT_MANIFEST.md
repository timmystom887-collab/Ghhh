# PROJECT_MANIFEST.md

This document lists every file generated for the Autonomous Android AI Agent project in order of generation.

1. `metadata.json` - Platform metadata
2. `PROJECT_MANIFEST.md` - Project file manifest
3. `build.gradle.kts` - Project-level Gradle configuration
4. `settings.gradle.kts` - Gradle settings and repositories
5. `gradle.properties` - Gradle project properties
6. `app/build.gradle.kts` - App-level Gradle configuration with dependencies
7. `app/src/main/AndroidManifest.xml` - Manifest with permissions, services, receivers, and accessibility service declaration
8. `app/src/main/res/values/strings.xml` - String resources and app name
9. `app/src/main/res/values/colors.xml` - Material 3 color palette
10. `app/src/main/res/values/themes.xml` - App theme configuration
11. `app/src/main/res/xml/accessibility_service_config.xml` - Accessibility service configuration
12. `app/src/main/res/xml/file_paths.xml` - File provider paths for secure sharing
13. `app/src/main/java/com/example/agent/MainActivity.kt` - Single activity hosting the chat UI and navigation
14. `app/src/main/java/com/example/agent/AgentApp.kt` - Application class initializing Hilt and WorkManager
15. `app/src/main/java/com/example/agent/data/local/AgentDatabase.kt` - Room database with SQLCipher encryption support
16. `app/src/main/java/com/example/agent/data/local/entity/MessageEntity.kt` - Chat message entity
17. `app/src/main/java/com/example/agent/data/local/entity/ProfileEntity.kt` - User profile entity for form-filling and onboarding
18. `app/src/main/java/com/example/agent/data/local/entity/TaskEntity.kt` - Scheduled and automated task entity
19. `app/src/main/java/com/example/agent/data/local/dao/MessageDao.kt` - Message DAO
20. `app/src/main/java/com/example/agent/data/local/dao/ProfileDao.kt` - Profile DAO
21. `app/src/main/java/com/example/agent/data/local/dao/TaskDao.kt` - Task DAO
22. `app/src/main/java/com/example/agent/data/datastore/PreferencesManager.kt` - Encrypted DataStore preferences manager
23. `app/src/main/java/com/example/agent/data/repository/AgentRepository.kt` - Central repository for messages, profile, tasks, and API calls
24. `app/src/main/java/com/example/agent/data/remote/GeminiApiService.kt` - Retrofit service for Gemini and multi-provider LLM integration
25. `app/src/main/java/com/example/agent/service/AgentAccessibilityService.kt` - Accessibility service for UI automation and form-filling
26. `app/src/main/java/com/example/agent/service/AgentForegroundService.kt` - Foreground service for long-running agent tasks
27. `app/src/main/java/com/example/agent/service/TaskWorker.kt` - WorkManager worker for scheduled tasks and automation
28. `app/src/main/java/com/example/agent/ui/theme/Theme.kt` - Jetpack Compose M3 theme
29. `app/src/main/java/com/example/agent/ui/theme/Color.kt` - Compose color definitions
30. `app/src/main/java/com/example/agent/ui/theme/Type.kt` - Compose typography
31. `app/src/main/java/com/example/agent/ui/chat/ChatViewModel.kt` - ViewModel managing chat state, LLM interaction, and tool execution
32. `app/src/main/java/com/example/agent/ui/chat/ChatScreen.kt` - Main chat UI with message list, input bar, and inline cards
33. `app/src/main/java/com/example/agent/ui/chat/components/MessageCard.kt` - Chat message bubble component
34. `app/src/main/java/com/example/agent/ui/chat/components/OptionsMenuCard.kt` - Inline options menu card for settings and providers
35. `app/src/main/java/com/example/agent/ui/chat/components/SchedulingCard.kt` - Inline scheduling and automation card
36. `app/src/main/java/com/example/agent/ui/chat/components/ConfirmationCard.kt` - Consent and intent confidence confirmation card
37. `app/src/main/java/com/example/agent/ui/onboarding/OnboardingViewModel.kt` - First-run interview state machine ViewModel
38. `app/src/main/java/com/example/agent/ui/onboarding/OnboardingCard.kt` - Inline onboarding interview card component
39. `app/src/main/java/com/example/agent/util/PreThoughtEngine.kt` - Pre-thought protocol engine for planning and risk assessment
40. `app/src/main/java/com/example/agent/util/IntentConfidenceEvaluator.kt` - Intent confidence scoring and threshold verification
41. `app/src/main/java/com/example/agent/util/SecurityUtils.kt` - Encryption, secret redaction, and biometric security helpers
42. `app/src/main/java/com/example/agent/widget/AgentWidgetProvider.kt` - Home screen widget for emergency stop and quick commands
43. `app/src/main/java/com/example/agent/receiver/BootReceiver.kt` - Broadcast receiver to restore scheduled tasks on device reboot
44. `app/src/test/java/com/example/agent/IntentConfidenceTest.kt` - Unit tests for intent confidence evaluator
45. `app/src/test/java/com/example/agent/PreThoughtEngineTest.kt` - Unit tests for pre-thought engine
46. `app/src/main/java/com/example/agent/util/ThinkingMethodEngine.kt` - Cognitive Thinking Frameworks & Multi-Step Reasoning Engine
47. `app/src/main/java/com/example/agent/util/AutomatedSystemEngine.kt` - Autonomous Automated Systems & Logic To Follow Engine
48. `app/src/main/java/com/example/agent/ui/chat/components/ThinkingMethodsCard.kt` - Interactive Cognitive Thinking Methods UI Card
49. `app/src/main/java/com/example/agent/ui/chat/components/AutomatedSystemsCard.kt` - Interactive Automated Systems & Logic To Follow UI Card
50. `app/src/main/java/com/example/agent/ui/chat/components/GhostCallCard.kt` - Interactive SIP Telecom Bridge & DTMF Dialpad UI Card
51. `app/src/test/java/com/example/agent/ThinkingMethodEngineTest.kt` - Unit tests for cognitive thinking method engine
52. `app/src/test/java/com/example/agent/AutomatedSystemEngineTest.kt` - Unit tests for automated systems engine
53. `app/src/test/java/com/example/agent/McpServerEngineTest.kt` - Unit tests for Awesome MCP server manager and tool execution
54. `BUILD_INSTRUCTIONS.md` - Complete build guide for release APK and Android Studio
