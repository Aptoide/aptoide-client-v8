import argparse
import json
import subprocess
import xml.etree.ElementTree as ET

def git_show(ref: str, path: str) -> str:
    return subprocess.check_output(["git", "show", f"{ref}:{path}"], text=True)

def parse_strings(xml_text: str) -> dict[str, tuple[str, bool]]:
    """
    Returns {name: (value, translatable)}
    Only handles <string> entries (not plurals/arrays).
    """
    root = ET.fromstring(xml_text)
    out = {}
    for node in root.findall("string"):
        name = node.attrib.get("name")
        if not name:
            continue
        translatable = node.attrib.get("translatable", "true").lower() != "false"
        out[name] = ((node.text or ""), translatable)
    return out

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--base-ref", required=True)
    ap.add_argument("--strings-path", required=True)
    ap.add_argument("--out", required=True)
    args = ap.parse_args()

    base_xml = git_show(args.base_ref, args.strings_path)
    head_xml = open(args.strings_path, "r", encoding="utf-8").read()

    base_map = parse_strings(base_xml)
    head_map = parse_strings(head_xml)

    changed = {}
    for key, (val, translatable) in head_map.items():
        if not translatable:
            continue
        if key not in base_map or base_map[key][0] != val:
            changed[key] = val

    with open(args.out, "w", encoding="utf-8") as f:
        json.dump(changed, f, ensure_ascii=False, indent=2)

if __name__ == "__main__":
    main()
