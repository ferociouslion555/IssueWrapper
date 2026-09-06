import markdown
import re

with open('DOCUMENTATION.md', 'r', encoding='utf-8') as f:
    text = f.read()

text = re.sub(
    r'\*\[INSERT SCREENSHOT OF THE TERMINAL SHOWING `BUILD SUCCESS` FOR TESTS HERE\]\*',
    r'![Test Output](C:/Users/algos/.gemini/antigravity-ide/brain/9f3fffcf-40fe-4bf6-b94d-dc9020e25805/.user_uploaded/media_1788586009854.png)',
    text
)

text = re.sub(
    r'\*\[INSERT SCREENSHOT OF THE TERMINAL SHOWING `Tomcat started on port 8080` HERE\]\*',
    r'',
    text
)

text = re.sub(
    r'\*\[INSERT SCREENSHOT OF GITHUB WEBHOOK SETTINGS UI SHOWING GREEN CHECKMARK\]\*',
    r'![Webhook Settings](C:/Users/algos/.gemini/antigravity-ide/brain/9f3fffcf-40fe-4bf6-b94d-dc9020e25805/.user_uploaded/media_1788586059948.png)',
    text
)

text = re.sub(
    r'\*\[INSERT SCREENSHOT OF POSTMAN OR TERMINAL \(cURL\) SHOWING 200 OK RESPONSE FOR `/issues`\]\*',
    r'![API Test](C:/Users/algos/.gemini/antigravity-ide/brain/9f3fffcf-40fe-4bf6-b94d-dc9020e25805/.user_uploaded/media_1788586129757.png)',
    text
)

text = re.sub(
    r'\*\[INSERT SCREENSHOT OF TERMINAL LOGS SHOWING \"Successfully processed issues event\.\.\.\" OR POSTMAN HITTING `/events`\]\*',
    r'![Webhook Delivery](C:/Users/algos/.gemini/antigravity-ide/brain/9f3fffcf-40fe-4bf6-b94d-dc9020e25805/.user_uploaded/media_1788586129741.png)',
    text
)

html = markdown.markdown(text)
full_html = '<html><head><title>Submission</title></head><body>' + html + '</body></html>'

with open('Submission.doc', 'w', encoding='utf-8') as f:
    f.write(full_html)

print('Submission.doc generated successfully!')
