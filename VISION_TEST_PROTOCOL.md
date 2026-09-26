# Vision-Capable Multi-Modal "Eyes-On" Testing Protocol

This document outlines the rigorous quality assurance protocol combining automated JVM/Robolectric unit testing with multi-modal vision-capable AI verification for the Autonomous Android AI Agent.

## 1. Automated JVM & Robolectric Test Suite
- **Location**: `app/src/test/java/com/example/agent/`
- **Execution**: Run local unit tests via Gradle:
  ```bash
  gradle :app:testDebugUnitTest
  ```
- **Coverage**:
  - `ExampleRobolectricTest.kt`: Tests Room database insertion/queries for messages, tasks, and memory entities, alongside `TaskOrchestrator` strategy routing.
  - `AgentUiTest.kt`: Tests Jetpack Compose UI rendering, theme application, and accessibility semantics using Robolectric Compose.

## 2. Vision-Capable "Eyes-On" Testing Protocol
For end-to-end graphical and visual verification of the streaming emulator UI:
1. **Streaming Preview Inspection**: Access the development preview URL (`https://ais-dev-grlk4q2bzrlim5s4bmi36j-342621886699.us-east5.run.app`).
2. **Visual Asset Check**: Verify adaptive launcher icon rendering, typography scale, Material 3 color schemes, and edge-to-edge layout constraints.
3. **Interactive CUJ Verification**:
   - **Onboarding**: Verify name, email, and phone input validation and setup completion card transition.
   - **Chat & Voice**: Test sending text messages, triggering speech recognition (`/mic`), and verifying TTS audio feedback.
   - **Quick Actions & Quick Controls**: Tap the horizontal quick action chips for flashlight toggle, Wi-Fi settings, Deep Research, and Multi-API task splitting.
   - **Sub-Agent Progress Indicator**: Verify real-time progress bar animation and sub-agent state cards during complex research tasks.
   - **Settings & Character v2**: Open settings, configure OpenAI/Anthropic API keys, and customize Agent name, personality, and tone.
4. **Verdict Reporting**: Capture screenshot artifacts from the streaming emulator, evaluate layout responsiveness across compact and expanded windows, and confirm PASS status.
