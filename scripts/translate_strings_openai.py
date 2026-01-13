import argparse
import json
import os
import re
import requests

OPENAI_URL = "https://api.openai.com/v1/responses"

PLACEHOLDER_PATTERNS = [
    r"%\d+\$[sdfox]",  # %1$s, %2$d etc
    r"%[sdfox]",       # %s, %d etc
]

def extract_placeholders(s: str) -> set[str]:
    found = set()
    for pat in PLACEHOLDER_PATTERNS:
        found.update(re.findall(pat, s))
    return found

def validate_placeholders(src: str, dst: str) -> bool:
    return extract_placeholders(src).issubset(extract_placeholders(dst))

def get_output_text(resp_json: dict) -> str:
    out = []
    for item in resp_json.get("output", []):
        for c in item.get("content", []):
            if c.get("type") == "output_text" and "text" in c:
                out.append(c["text"])
    return "\n".join(out).strip()

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--in", dest="in_file", required=True)
    ap.add_argument("--source-locale", default="en")
    ap.add_argument("--locales", required=True)
    ap.add_argument("--model", default="gpt-4o-mini")
    ap.add_argument("--out", required=True)
    args = ap.parse_args()

    api_key = os.getenv("OPENAI_API_KEY")
    if not api_key:
        print("OPENAI_API_KEY not set; skipping translation.")
        with open(args.out, "w", encoding="utf-8") as f:
            json.dump({}, f, ensure_ascii=False, indent=2)
        return

    locales = [x.strip() for x in args.locales.split(",") if x.strip()]
    with open(args.in_file, "r", encoding="utf-8") as f:
        changed = json.load(f)

    if not changed:
        print("No changed strings; nothing to translate.")
        with open(args.out, "w", encoding="utf-8") as f:
            json.dump({}, f, ensure_ascii=False, indent=2)
        return

    schema = {
        "type": "object",
        "properties": {
            loc: {
                "type": "object",
                "additionalProperties": {"type": "string"}
            } for loc in locales
        },
        "required": locales,
        "additionalProperties": False
    }

    instructions = (
        "You are a professional software localizer.\n"
        "Translate Android string values.\n"
        "Rules:\n"
        "- Preserve placeholders exactly (e.g. %s, %1$s, %d).\n"
        "- Preserve any XML-like tags appearing in the string.\n"
        "- Do not add explanations.\n"
        "- Output MUST match the JSON schema.\n"
    )

    user_payload = {
        "source_locale": args.source_locale,
        "target_locales": locales,
        "strings": changed
    }

    body = {
        "model": args.model,
        "instructions": instructions,
        "input": [
            {"role": "user", "content": json.dumps(user_payload, ensure_ascii=False)}
        ],
        "text": {
            "format": {
                "type": "json_schema",
                "json_schema": {
                    "name": "android_translations",
                    "schema": schema,
                    "strict": True
                }
            }
        },
        "max_output_tokens": 4000,
        "temperature": 0.2
    }

    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json",
    }

    r = requests.post(OPENAI_URL, headers=headers, json=body, timeout=90)
    r.raise_for_status()
    resp = r.json()

    txt = get_output_text(resp)
    data = json.loads(txt)

    # Safety: if placeholders are broken, fallback to the original English
    for loc in locales:
        for k, src in changed.items():
            dst = data.get(loc, {}).get(k, "")
            if not dst or not validate_placeholders(src, dst):
                data.setdefault(loc, {})[k] = src

    with open(args.out, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

if __name__ == "__main__":
    main()
