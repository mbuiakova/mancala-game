document.addEventListener('DOMContentLoaded', () => {

    const showDemoButton = document.getElementById('showDemoButton');
    showDemoButton.addEventListener('click', () => {
        fetch('/demo', {
            method: 'POST'
        })
            .then(response => response.json())
            .then(data => {
                if (data.error) {
                    showError(data.error);
                } else if (data.moves) {
                    showError("");
                    animateMove(data.moves);
                } else {
                    console.error("Invalid response format:", data);
                }

                updateCurrentPlayer(data.currentPlayer);
            })
            .catch(error => console.error("Error fetching move data:", error));
    });

    function initializeBoard() {
        const pits = document.querySelectorAll('.pit, .mancala');

        pits.forEach(pit => {
            const index = pit.getAttribute('data-index');
            const stonesCount = parseInt(pit.getAttribute('data-stones'), 10);
            updatePit(pit, stonesCount);

            if (pit.classList.contains('pit')) {
                pit.addEventListener('click', () => {
                    fetch(`/move?pit=${index}`, {
                        method: 'POST'
                    })
                        .then(response => response.json())
                        .then(data => {
                            if (data.error) {
                                showError(data.error);
                            } else if (data.moves) {
                                showError("");
                                animateMove(data.moves);
                            } else {
                                console.error("Invalid response format:", data);
                            }

                            updateCurrentPlayer(data.currentPlayer);
                        })
                        .catch(error => console.error("Error fetching move data:", error));
                });
            }
        });
    }

    function updatePit(pit, stonesCount) {
        pit.innerHTML = ''; // Clear existing stones
        const stonesContainer = document.createElement('div');
        stonesContainer.classList.add('stones');

        for (let i = 0; i < stonesCount; i++) {
            const stone = document.createElement('div');
            stone.classList.add('stone');
            stone.classList.add(getStoneColor(i));
            stonesContainer.appendChild(stone);
        }
        pit.appendChild(stonesContainer);
    }

    function getStoneColor(index) {
        const colors = ['red', 'blue', 'green', 'yellow'];
        return colors[index % colors.length];
    }

    function animateMove(moves) {
        moves.forEach((move, index) => {
            setTimeout(() => {
                const sourcePit = document.querySelector(`[data-index='${move.fromPitIndex}'] .stones`);
                const targetPit = document.querySelector(`[data-index='${move.toPitIndex}'] .stones`);

                const oldSourcePitColor = sourcePit.parentElement.style.backgroundColor;
                sourcePit.parentElement.style.backgroundColor = 'green';
                const oldTargetPitColor = targetPit.parentElement.style.backgroundColor;
                targetPit.parentElement.style.backgroundColor = 'lightBlue';

                if (sourcePit && sourcePit.children.length > 0) {
                    const stone = sourcePit.children[0];
                    stone.style.transform = `translate(${targetPit.getBoundingClientRect().left - sourcePit.getBoundingClientRect().left}px, ${targetPit.getBoundingClientRect().top - sourcePit.getBoundingClientRect().top}px)`;

                    setTimeout(() => {
                        targetPit.appendChild(stone);
                        sourcePit.parentElement.style.backgroundColor = oldSourcePitColor;
                        targetPit.parentElement.style.backgroundColor = oldTargetPitColor;
                        stone.style.transform = '';
                    }, 500);
                }
            }, index * 600);
        });
    }

    function updateCurrentPlayer(player) {
        const currentPlayerElement = document.getElementById('currentPlayer');
        currentPlayerElement.textContent = `Player ${player}'s turn`;
    }

    function showError(message) {
        let errorDiv = document.querySelector('.error-message');
        if (!errorDiv) {
            errorDiv = document.createElement('div');
            errorDiv.classList.add('error-message');
            document.body.appendChild(errorDiv);
        }
        errorDiv.textContent = message;
    }

    initializeBoard(); // Initial setup
});
