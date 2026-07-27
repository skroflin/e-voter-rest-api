package com.skroflin.evoting_rest_api.util;

public final class EmailTemplateUtil {

    private EmailTemplateUtil() {
    }

    public static String buildVerificationEmailTemplate(String code) {
        return """
            <!DOCTYPE html>
            <html>

            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                        background-color: #f1f4f8;
                        margin: 0;
                        padding: 60px 20px;
                        color: #2d3748;
                        text-align: center;
                    }

                    .email-wrapper {
                        max-width: 500px;
                        margin: 0 auto;
                    }

                    .mail-icon {
                        width: 48px;
                        height: 48px;
                        margin: 0 auto 12px;
                        display: block;
                    }

                    .subtitle {
                        font-size: 18px;
                        color: #94a3b8;
                        margin-bottom: 28px;
                    }

                    .message-text {
                        font-size: 15px;
                        font-weight: 600;
                        line-height: 1.6;
                        color: #1e293b;
                        margin-bottom: 24px;
                    }

                    .code-input-box {
                        background-color: #ffffff;
                        border: 1px solid #e2e8f0;
                        border-radius: 6px;
                        padding: 12px 24px;
                        font-size: 18px;
                        letter-spacing: 2px;
                        color: #64748b;
                        display: inline-block;
                        width: 220px;
                        margin-bottom: 16px;
                        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
                    }

                    .expiry-banner {
                        background-color: #1e88e5;
                        color: #ffffff;
                        font-size: 14px;
                        padding: 12px 20px;
                        border-radius: 6px;
                        display: block;
                        width: 100%%;
                        max-width: 320px;
                        margin: 0 auto 32px;
                        box-sizing: border-box;
                    }

                    .footer-text {
                        font-size: 13px;
                        color: #cbd5e1;
                    }
                </style>
            </head>

            <body>
                <div class="email-wrapper">
                    <!-- Ikona omotnice -->
                    <svg class="mail-icon" viewBox="0 0 24 24" fill="none" stroke="#1e293b" stroke-width="1.8"
                        stroke-linecap="round" stroke-linejoin="round">
                        <rect x="2" y="4" width="20" height="16" rx="2"></rect>
                        <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"></path>
                    </svg>

                    <div class="subtitle">e-Voting System verification code.</div>

                    <div class="message-text">
                        Dear user,<br>
                        we received a request to verify your account.<br>
                        Your one-time verification code is:
                    </div>

                    <!-- Polje s generiranim kodom -->
                    <div class="code-input-box">%s</div>

                    <!-- Plavi gumb/traka s trajanjem -->
                    <div class="expiry-banner">
                        The code is valid for <strong>15 minutes</strong>
                    </div>

                    <div class="footer-text">
                        This is an automated message. Please do not reply to this email.
                    </div>
                </div>
            </body>

            </html>
            """.formatted(code);
    }
}