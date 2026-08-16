# Contributing

## Thank You
Thank you for being interested in contributing to the project! It wouldn't be where it is today without the help of the community. This document will help you get started with contributing to the project.

## Links
- [Website](https://dansplugins.com)
- [Discord](https://discord.gg/xXtuAQ2)

## Requirements
- A GitHub account
- Git installed on your local machine
- A text editor or IDE
- A basic understanding of Java

## Getting Started
- If you don't already have a GitHub account, you can sign up for one [here](https://github.com/signup).
- Fork the repository on GitHub by clicking the "Fork" button on the top right of the repository page.
- Clone your fork of the repository to your local machine using `git clone https://www.github.com/<your-username>/FoodSpoilage.git`
- Open the project in your preferred text editor or IDE.
- Try compiling the plugin using the following command:
  ```bash
  ./gradlew build
  ```
  If you encounter any errors, please create an issue for it.

## Identifying What To Work On
### Issues
Work items are tracked as GitHub issues. You can find a full list of issues [here](https://github.com/Dans-Plugins/FoodSpoilage/issues).

### Milestones
Work items are organized into milestones, which represent a specific version of the plugin. You can find the milestones [here](https://github.com/Dans-Plugins/FoodSpoilage/milestones).

## Making Changes

1. Make sure an issue exists for the work. If not, create one.
2. Switch to `develop`: `git checkout develop`
3. Create a branch: `git checkout -b <branch-name>`
4. Make your changes.
5. Test your changes (see [Testing](#testing)).
6. Commit: `git commit -m "Description of changes"`
7. Push: `git push origin <branch-name>`
8. Open a pull request against `develop`, link the related issue with `#<number>`.
9. Address review feedback.

## Testing

Run the build to verify your changes compile correctly:

Linux: `./gradlew clean build`  
Windows: `.\gradlew.bat clean build`

The automated tests live in `src/test/java` and are executed as part of that build; they can also be run on their own with `./gradlew test`.

For manual testing, start a local Spigot server:

```bash
docker compose up
```

## Questions
If you have any questions about contributing to the project, feel free to ask in the Discord server. You can join the Discord server [here](https://discord.gg/xXtuAQ2). This is the best place to ask questions.
