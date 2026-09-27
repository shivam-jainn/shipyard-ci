def call(Map config = [:]) {
    def evalPath = config.get('evalPath', 'evalset/')
    def agent = config.get('agent', 'claude-code')
    def model = config.get('model', 'gpt-4o')

    stage("Shipyard Eval (${agent})") {
        echo "Running Shipyard on ${evalPath} with ${agent}..."
        def extraFlags = ""
        if (agent) extraFlags += " --agent ${agent}"
        if (model) extraFlags += " --model ${model}"

        sh """
            export PATH=\$PATH:\$(go env GOPATH)/bin
            shipyard run ${evalPath} ${extraFlags}
        """
    }
}
