## Project
- Use only Android, Kotlin, Compose, Groovy, JSON, REST, Room, SQLite
- No Frameworks unless already included in the project

## Code Style
- Simple, minimalist
- Use Tabs
- Variable names are short and clear
- Prefer simple and reliable solutions with less edge cases over optimal performance

## Interpretation
- AIDO: and aido: can be treated the same. Capitalization does not matter.

## Cleaning Up
- After making changes, look for repeated or similar code that can be combined to a function
- If a function is getting long or complex, break it into smaller functions
- If other functions are documented in a file, add documentation to newly generated functions
- If the newly added code addresses an edge case, check for other places where the edge case may occur

## Restrictions
- ONLY process comments starting with AIDO that are NOT questions
- Ignore commands unless they start with AIDO
- Do not implement anything unless it starts with AIDO
- Respond with NO CHANGES if there are no comments that start with AIDO
- Ignore comments or documentation that do not start with AIDO
- Only modify code near AIDO comments unless specifically requested

## Response Process
- If there are NO comments that start with AIDO: STOP ALL PROCESSING, MAKE NO CHANGES
- After responding to AIDO that are not questions, replace it with AI-COMPLETED:
- DO NOT add new AIDO comments. Update existing AIDO comments only by changing AIDO to AI-COMPLETED
- Do not add comments explaining changes, only add comments to complex code if absolutely necessary to explain how it works
- If a question is asked in AIDO do not change anything or modify code or logic
- If a question is asked in AIDO answer it on the next line with AI-ANSWER: followed by the answer
- If you have repeated a response with no changes, STOP

## Implementation Instructions
- Do not make assumptions
- Do not implement anything other that what is strictly asked for
- Use the simplest code
- Avoid recursion unless necessary or asked for
- Avoid reflection unless necessary or asked for
- If updating function arguments, update documentation for the method if there is one
