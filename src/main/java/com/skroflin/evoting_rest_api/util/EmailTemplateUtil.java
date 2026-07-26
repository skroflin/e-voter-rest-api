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
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background: linear-gradient(135deg, #f4f7fb 0%%, #eef4ff 100%%);
                        margin: 0;
                        padding: 32px 16px;
                        color: #1f2937;
                    }

                    .email-container {
                        max-width: 560px;
                        margin: 0 auto;
                        background-color: #ffffff;
                        border-radius: 18px;
                        overflow: hidden;
                        box-shadow: 0 16px 40px rgba(15, 23, 42, 0.08);
                        border: 1px solid #e5e7eb;
                    }

                    .email-header {
                        background: linear-gradient(135deg, #4b5563 0%%, #6b7280 100%%);
                        color: #ffffff;
                        padding: 28px 24px 24px;
                        text-align: center;
                    }

                    .icon-circle {
                        width: 54px;
                        height: 54px;
                        margin: 0 auto 12px;
                        border-radius: 50%%;
                        background: rgba(255, 255, 255, 0.16);
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        backdrop-filter: blur(4px);
                    }

                    .icon-circle svg {
                        width: 26px;
                        height: 26px;
                        fill: #ffffff;
                    }

                    .email-header h1 {
                        margin: 0;
                        font-size: 24px;
                        font-weight: 700;
                        letter-spacing: 0.3px;
                    }

                    .email-body {
                        padding: 34px 28px 28px;
                        text-align: center;
                    }

                    .email-body p {
                        font-size: 15px;
                        line-height: 1.7;
                        margin: 0 0 22px;
                        color: #4b5563;
                    }

                    .code-box {
                        background: linear-gradient(135deg, #f8f9fb 0%%, #f1f3f6 100%%);
                        border: 2px dashed #6b7280;
                        border-radius: 12px;
                        padding: 18px 16px;
                        font-size: 30px;
                        font-weight: 800;
                        letter-spacing: 8px;
                        color: #4b5563;
                        display: inline-block;
                        margin: 4px 0 12px;
                        min-width: 220px;
                    }

                    .badge-expiry {
                        font-size: 13px;
                        color: #6b7280;
                        background-color: #f9fafb;
                        padding: 10px 14px;
                        border-radius: 999px;
                        display: inline-flex;
                        align-items: center;
                        justify-content: center;
                        gap: 8px;
                        border: 1px solid #e5e7eb;
                        width: fit-content;
                        margin: 0 auto;
                    }

                    .badge-expiry svg {
                        width: 16px;
                        height: 16px;
                        fill: #6b7280;
                        flex-shrink: 0;
                    }

                    .email-footer {
                        background-color: #f9fafb;
                        padding: 16px 20px 20px;
                        text-align: center;
                        font-size: 12px;
                        color: #9ca3af;
                        border-top: 1px solid #f3f4f6;
                    }
                </style>
            </head>

            <body>
                <div class="email-container">
                    <div class="email-header">
                        <div class="icon-circle" aria-hidden="true">
                            <svg viewBox="0 0 24 24">
                                <path d="M12 2a7 7 0 0 0-7 7c0 3.87 2.57 7.2 6 8.24V20H8.5a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h7a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5H13v-2.76A8.99 8.99 0 0 0 19 9a7 7 0 0 0-7-7Zm0 2a5 5 0 0 1 5 5 5 5 0 0 1-5 5 5 5 0 0 1-5-5 5 5 0 0 1 5-5Z"/>
                            </svg>
                        </div>
                        <h1>E-Voting System</h1>
                    </div>
                    <div class="email-body">
                        <p>Dear user,<br>we received a request to verify your account. Your one-time verification code is:</p>
                        
                        <div class="code-box">%s</div>
                        
                        <div>
                            <div class="badge-expiry">
                                <svg viewBox="0 0 24 24" aria-hidden="true">
                                    <path d="M12 2a10 10 0 1 0 10 10A10.01 10.01 0 0 0 12 2Zm1 5h-2v6l5 3 1-1.73-4-2.27Z"/>
                                </svg>
                                <span>This code is valid for <strong>15 minutes</strong></span>
                            </div>
                        </div>
                    </div>
                    <div class="email-footer">
                        This is an automated message. Please do not reply to this email.
                    </div>
                </div>
            </body>

            </html>
            """.formatted(code);
    }
}