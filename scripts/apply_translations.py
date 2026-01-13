import argparse
import json
import os
import xml.etree.ElementTree as ET

def load_or_create(path: str) -> ET.ElementTree:
    if os.path.exists(path):
        return ET.parse(path)
    root = ET.Element("resources")
    return ET.ElementTree(root)

def upsert_string(root: ET.Element, name: str, value: str):
    for node in root.findall("string"):
        if node.attrib.get("name") == name:
            node.text = value
            return
    node = ET.SubElement(root, "string", {"name": name})
    node.text = value

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--translations", required=True)
    ap.add_argument("--res-dir", required=True)
    args = ap.parse_args()

    with open(args.translations, "r", encoding="utf-8") as f:
        translations = json.load(f)

    for locale, items in translations.items():
        if not items:
            continue

        values_dir = os.path.join(args.res_dir, f"values-{locale}")
        os.makedirs(values_dir, exist_ok=True)

        strings_path = os.path.join(values_dir, "strings.xml")
        tree = load_or_create(strings_path)
        root = tree.getroot()

        for k, v in items.items():
            upsert_string(root, k, v)

        tree.write(strings_path, encoding="utf-8", xml_declaration=True)

if __name__ == "__main__":
    main()
