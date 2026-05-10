# WorkChecker

A personal Android app that logs check-in / check-out events by tapping an NFC tag — a private mirror of the company's attendance machine, so you never have to wonder *"did I clock in?"* again.

Tap the phone on an NFC tag placed next to the office attendance machine; the app fires an HTTP POST to a webhook of your choice (Discord, Slack, Apps Script, n8n, ...).

## How it works

```
Tap NFC tag → NDEF dispatch → NFCResultActivity → POST webhook → result overlay (auto-closes 2s)
```

The NFC tag only needs one NDEF URI record:

```
workchecker://checkcheck

```
Optional - Android Application Record (AAR) to prevent devices without the app installed from responding to the NFC tag.

```
com.thanhdv.workchecker
```

Write it with any tag-writer app (e.g. NFC Tools).

## Requirements

- Android 7.0 (API 24)+ with NFC hardware
- A writable NFC tag (NTAG213/215/216)
- A webhook endpoint

## Configuration

Open the app and fill in the **Configuration** screen:

| Field         | Notes                                                     |
|---------------|-----------------------------------------------------------|
| Full Name     | Used as `{name}`                                          |
| Email         | Used as `{email}`                                         |
| Employee ID   | Used as `{empId}`                                         |
| Webhook URL   | Masked on screen to avoid leaking tokens                  |
| Payload       | JSON body sent on each tap; supports placeholders below   |

### Payload placeholders

| Placeholder  | Value                                                          |
|--------------|----------------------------------------------------------------|
| `{name}`     | Full Name                                                      |
| `{empId}`    | Employee ID                                                    |
| `{email}`    | Email                                                          |
| `{time}`     | Local time, `HH:mm dd/MM/yyyy`                                 |
| `{isoTime}`  | UTC ISO 8601 (`yyyy-MM-dd'T'HH:mm:ss.SSS'Z'`)                  |
| `{message}`  | Rendered from `{name} ({empId}) checked at {time}`             |

### Example — Discord

```json
{
  "embeds": [{
    "title": "Check-in",
    "description": "**Name:** {name}\n**ID:** {empId}\n**Email:** {email}",
    "timestamp": "{isoTime}"
  }]
}
```

### Example — Slack

```json
{ "text": "{message}" }
```

## Project structure

```
app/src/main/java/com/thanhdv/workchecker/
├── MainActivity.kt          # Compose configuration UI
├── NFCResultActivity.kt     # NDEF entry point + result overlay
├── data/                    # UserConfig + DataStore repository
├── network/WebhookClient.kt # OkHttp + template rendering
└── ui/theme/                # Material 3 theme + ConfigViewModel
```

## Tech stack

* Kotlin
* Jetpack Compose (Material 3) 
* DataStore Preferences 
* OkHttp 
* NFC NDEF dispatch

## License

MIT — see [LICENSE.md](LICENSE.md).
