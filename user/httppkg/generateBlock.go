package httppkg

import (
	"cabb/user/blockpkg"
	"encoding/json"
	"net/http"
)

type blkID struct {
	BlockID [32]byte `json:"BlockID"`
}

func CreateNewBlock(w http.ResponseWriter, req *http.Request) {
	prevHash := [32]byte{} // 가장 최근의 블록 해시를 불러와야 함
	height := 0            // 가장 최근 블록의 height 또는 블록체인의 길이를 저장
	txID := [32]byte{}     // req의 바디에 저장된 json 데이터를 가지고 와야함
	data := ""             // req의 바디에 저장된 json 데이터를 가지고 와야함
	res := &blkID{}
	res.BlockID = blockpkg.NewBlock(prevHash, height, txID, data).Hash
	json.NewEncoder(w).Encode(res)
}
