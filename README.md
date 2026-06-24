# AI Color Themes
## An Android Application Demo

### About the Application

This is a simple demo application that uses an LLM to generate color themes.

The user can describe the theme, and optionally provide a base color.

The application uses Compose for the UI and navigation,
DataStore for saving settings, and a simple HTTP request for networking.

This application was specifically designed to use a simple architecture
and minimal third-party libraries.

### A Note Regarding AI and AGENTS.md

It is virtually impossible to completely avoid AI in development today.
Even if you remove every extension from Android Studio, bots on GitHub will attempt to create pull requests.

The AGENTS.md file included in this project is intended as a happy medium.
It strictly limits AI agents to comments that start with a keyword,
and provides further guidelines to prevent runaway submissions.

This achieves two goals.
First, the developer can still use AI for simple tasks such as generating boilerplate code or analyzing code.
Second, it prevents bots on GitHub from assuming that they should contribute features.

As a developer, I strongly believe that AI is a tool, not a replacement for
the experience and judgment of an engineer.
As such, every line in this application has been written, edited,
or thoroughly reviewed by the developer, regardless of simplicity.
Every class, interface, package, and preview was created deliberately.
Every comment and test has been written, read, and validated by a human.

