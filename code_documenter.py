import argparse

import javalang
import openai

start_sequence = "\n\"\"\"\n\nI rephrased it for him, in plain language a second grader can understand:\n\"\"\"\n"

if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description="Invokes GPT-3 to obtain the explanation of what a given piece of code does")
    parser.add_argument('token', type=str, help="OpenAI token")
    parser.add_argument('input_path', type=str, help="file containing the code snippet")
    args = parser.parse_args()
    openai.api_key = args.token
    input_path = args.input_path
    with open(input_path, 'r') as f:
        java_func = "".join(f.readlines())

    tokenized = javalang.tokenizer.tokenize(java_func)
    short_form = " ".join([token.value for token in tokenized])

    def_start = "My colleague asked me what this Java code does:\n"
    separator = f'\n\"\"\"\n'
    injected_prompt = "I explained it to him, in plain language:\n"
    prompt = def_start \
             + separator \
             + short_form \
             + separator \
             + injected_prompt \
             + separator
    print(f'Prompt:\n{prompt}')
    response = openai.Completion.create(
        engine="davinci-instruct-beta",
        prompt=prompt,
        temperature=0.6,
        max_tokens=100,
        top_p=1,
        best_of=3,
        frequency_penalty=0.2,
        stop=["\n"],
    )

    print(response.choices)
