---
description: Android Development Guidelines & Automation
---
# Android Development Workflow

Whenever I am asked to develop, modify, or review Android code in this project, I will strictly adhere to the following guidelines and utilize these tools.

## 1. UI & Components (Jetpack Compose / XML)
- **Jetpack Compose First**: Give priority to Jetpack Compose for new UI elements unless XML is specifically requested or required by existing legacy code.
- **Material Design 3**: Follow Material 3 (Material You) design specifications for components, typography, and color schemes to ensure a modern, premium look.
- **Component Libraries**: When applicable, use standard AndroidX libraries and components rather than recreating basic UI elements. Search for existing custom components in the project tree before building new ones.

## 2. Architecture & Code Quality
- **MVVM / MVI**: Enforce separation of concerns using ViewModel. UI state should be exposed via `StateFlow` or `LiveData` (prefer `StateFlow` for Kotlin-first development).
- **Coroutines & Flow**: Use Kotlin Coroutines and Flow for all asynchronous operations instead of RxJava or callbacks, unless interfacing with legacy code.
- **Naming Conventions**: Follow standard Kotlin & Android naming conventions (e.g., `CamelCase` for classes, `camelCase` for variables/functions).

## 3. Knowledge Retrieval (NotebookLM)
- Use the **NotebookLM Skill** (`/notebooklm`) to retrieve project-specific documentation, PRDs, UX guidelines, or any complex reference materials if they have been uploaded to a NotebookLM notebook.
- **Trigger**: I will automatically check or ask to consult NotebookLM if the user mentions "references," "docs," or "NotebookLM notebooks."

## 4. Verification & Compilation Rule
- **NEVER AUTOMATICALLY COMPILE OR BUILD THE PROJECT**.
- Do not run `./gradlew assembleDebug`, `assembleRelease`, or any other compilation commands autonomously.
- The USER will explicitly handle all compilation and build verification steps themselves. My job is strictly writing and modifying code.
