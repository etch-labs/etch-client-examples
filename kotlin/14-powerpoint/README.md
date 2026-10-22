# 14 PowerPoint

This example renders `templates/project-update.pptx`, a PowerPoint deck, with the data in
`data/projects.json`. The program is the same as the quick start apart from the template: the output
is a `.pptx` because the template is one.

The template has three slides, and the rendered deck has five: a title slide, one slide for each of
the three projects, and a summary.

| In the template | What it does |
|---|---|
| `{{#slide-each projects}}` and `{{/slide-each}}` on the middle slide | Copies that slide once per project; every text box on the copy is filled from that project |
| `{{#each milestones}}` and `{{/each}}` on paragraphs of their own | Repeats the paragraph between them: one line per milestone |
| `{{#if done}}Done{{else}}To do{{/if}}` | Marks each milestone |
| `{{notes}}` in the slide's speaker notes | Speaker notes are filled from the data too |
| `{{@index1}} of {{length $root.projects}}` | "1 of 3": loop variables and `$root` work inside a slide loop |
| `{{#each projects}}...{{/each}}` across a table row | One table row per project on the summary slide |
| `{{length (filter projects "status" "On track")}}` | "2 on track" on the title slide |

The marker paragraphs leave nothing behind, and the slides keep the template's fonts, colours and
layout.

To learn more about PowerPoint templates, see PowerPoint under Template syntax in the
[ETCH documentation](https://get-etch.app/docs).

The code is in [Powerpoint.kt](Powerpoint.kt).

## Run

From the `kotlin` folder, after following [INSTALL.md](../INSTALL.md):

```bash
./gradlew run -Pexample=14-powerpoint
```

The example writes the deck to `14-powerpoint/output/`.
