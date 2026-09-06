import re
import base64
import os

with open('Submission.doc', 'r', encoding='utf-8') as f:
    html = f.read()

def replacer(match):
    path = match.group(1)
    if os.path.exists(path):
        with open(path, 'rb') as img_file:
            encoded_string = base64.b64encode(img_file.read()).decode('utf-8')
        return f'src="data:image/png;base64,{encoded_string}"'
    return match.group(0)

# Replace src="..." with base64 embedded
new_html = re.sub(r'src="([^"]+)"', replacer, html)

with open('Submission.doc', 'w', encoding='utf-8') as f:
    f.write(new_html)

print("Images successfully embedded into Submission.doc via Base64!")
